package com.saeedshahab.bashdown.wrappers.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.saeedshahab.bashdown.core.BashConfiguration;
import com.saeedshahab.bashdown.models.cucumber.Model;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import com.saeedshahab.bashdown.wrappers.mongo.MongoWrapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.dropwizard.jackson.Jackson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseWrapperSteps {

    private DatabaseWrapper<?> databaseWrapper;
    private Model model;
    private Boolean databaseHealth;

    public DatabaseWrapperSteps() throws Exception {
        ObjectMapper mapper = Jackson.newObjectMapper(new YAMLFactory());
        BashConfiguration bashConfiguration = mapper.readValue(Files.readAllBytes(Paths.get("server.yml")), BashConfiguration.class);

        databaseWrapper = new MongoWrapper(bashConfiguration);
    }

    @Given("^that a model with id: (.+) (?:is|should be) inserted$")
    public void insertModelWithId(String id) {
        Model model = new Model();
        model.setId(id);
        databaseWrapper.create(model, Model.class);
    }

    @When("^the model with id: (.+) (?:is|should be) deleted$")
    public void deleteModelWithId(String id) {
        model = databaseWrapper.deleteById(id, Model.class);
    }

    @Then("^the model with id: (.+) (?:is|should be) present$")
    public void assertModelPresentWithId(String id) {
        assertThat(model.getId()).isEqualTo(id);
    }

    @Then("^the model with id: (.+) (?:is|should be) absent$")
    public void assertModelIsAbsentWithId(String id) {
        assertThat(databaseWrapper.getById(id, Model.class)).isNull();
    }

    @When("^a model with id: (.+) (?:is|should be) looked up$")
    public void searchModelWithId(String id) {
        List<Model> models = databaseWrapper.search(Collections.singletonMap("id", id), Model.class);
        model = models.get(0);
    }

    @Given("^that the database of model (?:is|should be) dropped$")
    public void dropDatabase() {
        databaseWrapper.dropDatabase(Model.class);
    }

    @When("^the model with id: (.+) (?:is|should be) replaced with id: (.+)$")
    public void replaceModel(String query, String replace) throws Throwable {
        databaseWrapper.findAndUpdate(Collections.singletonMap("id", query), Collections.singletonMap("id", replace), Model.class);
    }

    @When("^the database (?:is|should be) pinged$")
    public void pingDatabase() {
        databaseHealth = databaseWrapper.health();
    }

    @Then("^the database (?:is|should be) healthy$")
    public void databaseHealthy() {
        assertThat(databaseHealth).isTrue();
    }
}
