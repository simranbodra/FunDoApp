package com.bridgelabz.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.bridgelabz.fundoonotes.note.models.Label;

public interface LabelElasticsearchRepository extends ElasticsearchRepository<Label, String> {

	public Optional<Label> findByLabelName(String labelName);

	public List<Label> findAllByUserId(String userId);

	public List<Label> findAllByLabelName(String labelName);
}
