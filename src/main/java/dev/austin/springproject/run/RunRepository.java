package dev.austin.springproject.run;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Repository
public class RunRepository {

    private static final Logger log = LoggerFactory.getLogger(RunRepository.class);
    private final List<Run> runs = new ArrayList<>();
    private final JdbcClient jdbcClient;

    public RunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Run> findAll() {
        return jdbcClient.sql("select * from runs")
                .query(Run.class)
                .list();
    }

    public Optional<Run> findById(Integer id) {
        return jdbcClient.sql("select id, title, started_on, completed_on, kilometers, location from runs where id = :id")
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    public void create(Run run) {
        var update = jdbcClient.sql("insert into Run(id, title, started_on, completed_on, kilometers, location) values (?, ?, ?, ?, ?, ?) ")
                .param(List.of(run.id(), run.title(), run.startedOn(), run.completedOn(), run.kilometers(), run.location().toString()))
                .update();

        Assert.state(update == 1, "Failed to create run" + run.title());
    }

    public void update(Run run, Integer id) {
        var update = jdbcClient.sql("update Run set title = ?, started_on = ?, completed_on = ?, kilometers = ?, location = ? where id = ?")
                .param(List.of(run.title(), run.startedOn(), run.completedOn(), run.kilometers(), run.location().toString(), id))
                .update();

        Assert.state(update == 1, "Failed to create run" + run.title());
    }

    public void delete(Integer id) {
        var update = jdbcClient.sql("delete from Run where id = :id")
                .param("id", id)
                .update();

        Assert.state(update == 1, "Failed to delete run with id " + id);
    }

    public int count() {
        return jdbcClient.sql("select *  from Run")
                .query()
                .listOfRows().size();
    }

    public void saveAll(List<Run> runs) {
        runs.stream().forEach(run -> create(run));
    }

    public List<Run> findByLocation(String location) {
        return runs.stream()
                .filter(run -> Objects.equals(run.location(), location))
                .toList();
    }


    @PostConstruct
    private void init() {
        runs.add(new Run(1,
                "Monday Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                3,
                Location.INDOOR));

        runs.add(new Run(2,
                "Wednesday Evening Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(60, ChronoUnit.MINUTES),
                6,
                Location.INDOOR));
    }
}
