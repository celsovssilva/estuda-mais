package com.estudamais.backend.repository;

import com.estudamais.backend.entity.DiaProva;
import com.estudamais.backend.entity.Question;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository  extends JpaRepository<Question,String> {
    List<Question> findByDisciplina(String disciplina);

    List<Question> findByAno(Integer ano);
    @Query("SELECT q FROM Question q WHERE q.ano = :ano AND q.dia = :dia AND (q.disciplina IS NULL OR q.disciplina = :disciplina) AND (q.idioma IS NULL OR q.idioma = :idioma)")
    List<Question> buscarSimulado(@Param("ano") Integer ano, @Param("dia") DiaProva dia,@Param("disciplina") String disciplina ,@Param("idioma")  String idioma);
}
