package exercisetracker.bootstrap;

import exercisetracker.model.Exercise;
import exercisetracker.repository.ExerciseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ExerciseRepository exerciseRepository) {

        return args -> {
            exerciseRepository.save(new Exercise("barbell squat", "quads"));
            exerciseRepository.save(new Exercise("barbell romanian deadlift", "hamstrings"));

            exerciseRepository.findAll().forEach(exercise -> log.info("Preloaded " + exercise));
        };
    }
}
