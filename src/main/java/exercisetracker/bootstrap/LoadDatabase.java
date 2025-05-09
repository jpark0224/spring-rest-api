package exercisetracker.bootstrap;

import exercisetracker.model.ExerciseTemplate;
import exercisetracker.repository.ExerciseTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(ExerciseTemplateRepository exerciseTemplateRepository) {

        return args -> {
            exerciseTemplateRepository.save(new ExerciseTemplate("barbell squat", "quads"));
            exerciseTemplateRepository.save(new ExerciseTemplate("barbell romanian deadlift", "hamstrings"));

            exerciseTemplateRepository.findAll().forEach(exercise -> log.info("Preloaded " + exercise));
        };
    }
}
