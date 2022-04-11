package cambio.simulator.test.consistency;

import java.io.File;
import java.io.IOException;

import cambio.simulator.test.FileLoaderUtil;
import cambio.simulator.test.TestBase;
import org.junit.jupiter.api.Test;

/**
 * @author Lion Wagner
 */
public class Reproducibility extends TestBase {


    @Test
    void ChaosMonkeyReproducibility() throws IOException {
        File exp = FileLoaderUtil.loadFromExampleResources("PaperExample","paper_experiment.json");
        File arch = FileLoaderUtil.loadFromExampleResources("PaperExample","paper_architecture.json");
        testReproducibility(exp, arch);
    }


    @Test
    void CircuitbreakerReproducibility() throws IOException {
        File arch = FileLoaderUtil.loadFromExampleResources("ProofOfConcept", "arch_circuitbreaker_demonstration.json");
        File exp = FileLoaderUtil.loadFromExampleResources("ProofOfConcept", "exp_circuitbreaker_demonstration.json");
        testReproducibility(exp, arch);
    }

    @Test
    void RetryReproducibility() throws IOException {
        File arch = FileLoaderUtil.loadFromTestResources("SSPExample", "ssp_architecture.json");
        File exp = FileLoaderUtil.loadFromTestResources("SSPExample", "ssp_experiment.json");
        testReproducibility(exp, arch);
    }
}
