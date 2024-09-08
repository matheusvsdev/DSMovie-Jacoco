package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private UserService userService;

	private UserEntity user;
	private MovieEntity movie;
	private ScoreEntity score;
	private ScoreDTO scoreDTO;
	private Double scoreValue;


	@BeforeEach
	void setUp() {
		scoreValue = 4.5;

		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		score = ScoreFactory.createScoreEntity();

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movie));
		Mockito.when(scoreRepository.saveAndFlush(Mockito.any(ScoreEntity.class))).thenReturn(score);
		Mockito.when(movieRepository.save(Mockito.any(MovieEntity.class))).thenReturn(movie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		ScoreEntity existingScore = new ScoreEntity();
		existingScore.setMovie(movie);
		existingScore.setUser(user);
		existingScore.setValue(3.75);

		movie.getScores().add(existingScore);

		ScoreDTO newScoreDTO = new ScoreDTO(movie.getId(), scoreValue);

		Mockito.when(scoreRepository.saveAndFlush(Mockito.any(ScoreEntity.class))).thenReturn(score);
		Mockito.when(movieRepository.save(Mockito.any(MovieEntity.class))).thenReturn(movie);

		MovieDTO result = service.saveScore(newScoreDTO);

		double expectedAverage = (3.0 + scoreValue) / 2;

		Assertions.assertNotNull(result);
		Assertions.assertEquals(expectedAverage, result.getScore(), 0.01);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
