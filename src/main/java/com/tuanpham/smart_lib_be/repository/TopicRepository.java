package com.tuanpham.smart_lib_be.repository;

import com.tuanpham.smart_lib_be.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String>, JpaSpecificationExecutor<Topic> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
    List<Topic> findByIdIn(List<String> ids);
    @Query(value = "select count(topic_id) from topic_publication where topic_id= ?1", nativeQuery = true)
    int countTopicPublication(String topic_id);
}
