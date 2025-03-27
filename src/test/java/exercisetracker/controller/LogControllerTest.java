package exercisetracker.controller;

import exercisetracker.assembler.LogModelAssembler;
import exercisetracker.model.Log;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @Mock
    private LogModelAssembler assembler;

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogController logController;

    @Test
    void testAll_returnsCollectionModelWithLogs() {
        Log log1 = new Log();
        log1.setId(1L);

        Log log2 = new Log();
        log2.setId(2L);

        List<Log> logs = List.of(log1, log2);

        EntityModel<Log> logModel1 = EntityModel.of(log1);
        logModel1.add(Link.of("/logs/1").withSelfRel());

        EntityModel<Log> logModel2 = EntityModel.of(log2);
        logModel2.add(Link.of("/logs/2").withSelfRel());

        when(logRepository.findAll()).thenReturn(logs);
        when(assembler.toModel(log1)).thenReturn(logModel1);
        when(assembler.toModel(log2)).thenReturn(logModel2);

        CollectionModel<EntityModel<Log>> result = logController.all();

        assertThat(result.getContent()).containsExactlyInAnyOrder(logModel1, logModel2);
        assertThat(result.getLinks())
                .anyMatch(link -> link.getRel().value().equals("self"));

        result.getContent().forEach(model ->
                assertThat(model.getLinks())
                        .anyMatch(link -> link.getRel().value().equals("self"))
        );
    }

}
