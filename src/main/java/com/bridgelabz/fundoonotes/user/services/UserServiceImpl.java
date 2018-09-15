package com.bridgelabz.fundoonotes.user.services;

import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Mail;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.User;
import com.bridgelabz.fundoonotes.user.models.UserProfile;
import com.bridgelabz.fundoonotes.user.rabbitmq.MailProducerService;
import com.bridgelabz.fundoonotes.user.repositories.TokenRepository;
import com.bridgelabz.fundoonotes.user.repositories.UserElasticsearchRepository;
import com.bridgelabz.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.fundoonotes.user.utility.JWTokenProvider;
import com.bridgelabz.fundoonotes.user.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	private static final String SUFFIX = "/";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserElasticsearchRepository userElasticsearchRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTokenProvider tokenProvider;

	@Autowired
	private MailProducerService producer;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private ImageStorageService imageStorageService;

	@Value("${activationLink}")
	private String activationLink;

	@Value("${resetPasswordLink}")
	private String resetPasswordLink;

	@Autowired
	private Environment environment;

	@Value("${defaultProfileImage}")
	private String defaultProfileImage;

	@Value("${profilePictures}")
	private String profilePictures;

	@Override
	public void register(Registration registrationDto) throws RegistrationException, MessagingException {
		UserUtility.validateUserForRegistration(registrationDto);

		if (userRepository.findByEmail(registrationDto.getEmailId()).isPresent()) {
			throw new RegistrationException(environment.getProperty("EmailAlreadyRegistered"));
		}

		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());

		String password = passwordEncoder.encode(registrationDto.getPassword());

		user.setPassword(password);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

		Optional<User> optionalUser = userRepository.findByEmail(registrationDto.getEmailId());
		User updateUser = optionalUser.get();
		String userId = user.getUserId();

		String folder = userId + SUFFIX + profilePictures;

		String defaultPicture = imageStorageService.getFile(folder, defaultProfileImage);

		updateUser.setProfileImage(defaultPicture);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

		String token = tokenProvider.tokenGenerator(user.getUserId());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject(environment.getProperty("AccountActivationMail"));
		mail.setBody(activationLink + token);

		producer.send(mail);
	}

	@Override
	public String login(Login loginDto) throws LoginException, UserNotFoundException, UserNotActivatedException {
		UserUtility.validateUserForLogin(loginDto);

		Optional<User> user = userRepository.findByEmail(loginDto.getEmail());

		if (!user.isPresent()) {
			throw new UserNotFoundException(environment.getProperty("EmailNotRegistered"));
		}

		if (!user.get().isActive()) {
			throw new UserNotActivatedException(environment.getProperty("AccountNotActive"));
		}

		if (!passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
			throw new UserNotFoundException(environment.getProperty("IncorrectPassword"));
		}

		String token = tokenProvider.tokenGenerator(user.get().getUserId());

		return token;

	}

	@Override
	public void activate(String token) throws LoginException {
		String userId = tokenProvider.parseJWT(token);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		user.setActive(true);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

	}

	@Override
	public void sendPasswordLink(String email) throws MessagingException, UserNotFoundException {

		Optional<User> user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			throw new UserNotFoundException(environment.getProperty("EmailNotRegistered"));
		}

		String token = UserUtility.generateUUID();

		tokenRepository.save(token, user.get().getUserId());

		Mail mail = new Mail();
		mail.setTo(email);
		mail.setSubject(environment.getProperty("PasswordResetMail"));
		mail.setBody(resetPasswordLink + token);

		producer.send(mail);
	}

	@Override
	public void passwordReset(String token, ResetPassword resetPassword) throws LoginException, UserNotFoundException {
		UserUtility.validateUserForResetPassword(resetPassword);

		String userId = tokenRepository.get(token);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		String password = passwordEncoder.encode(resetPassword.getPassword());

		user.setPassword(password);
		user.setActive(true);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

	}

	@Override
	public String addProfilePicture(String token, MultipartFile image) throws FileConversionException {

		String userId = tokenProvider.parseJWT(token);

		String folder = userId + SUFFIX + profilePictures;

		imageStorageService.uploadFile(folder, image);

		String picture = imageStorageService.getFile(folder, image.getOriginalFilename());

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		user.setProfileImage(picture);

		userRepository.save(user);

		userElasticsearchRepository.save(user);

		return picture;
	}

	@Override
	public void removeProfilePicture(String token) {
		String userId = tokenProvider.parseJWT(token);
		
		String folder = userId + SUFFIX + profilePictures;

		String defaultPicture = imageStorageService.getFile(folder, defaultProfileImage);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		user.setProfileImage(defaultPicture);

		userRepository.save(user);

		userElasticsearchRepository.save(user);
	}

	@Override
	public UserProfile getProfileDetails(String token) {
		String userId = tokenProvider.parseJWT(token);

		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();

		UserProfile userProfile = new UserProfile();

		userProfile.setUserName(user.getName());
		userProfile.setUserEmail(user.getEmail());
		userProfile.setImageUrl(user.getProfileImage());

		return userProfile;
	}
}
