package com.estudamais.backend.repository;

import com.estudamais.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository  extends JpaRepository<Question,String> {
    List<Question> findByDisciplina(String disciplina);

    List<Question> findByAno(Integer ano);
}
