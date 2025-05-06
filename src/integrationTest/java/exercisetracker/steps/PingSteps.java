package exercisetracker.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class PingSteps {

    @Given("ping")
    public void ping() {
        System.out.println("ping");
    }

    @Then("pong")
    public void pong() {
        System.out.println("pong");
    }
}