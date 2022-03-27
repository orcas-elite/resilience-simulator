package cambio.simulator.entities.patterns;

import java.util.*;
import java.util.stream.Collectors;

import cambio.simulator.entities.microservice.MicroserviceInstance;
import cambio.simulator.entities.microservice.NoInstanceAvailableException;
import cambio.simulator.models.MiSimModel;
import cambio.simulator.parsing.JsonTypeName;

/**
 * Strategy that chooses the least utilized Microservice Instance by current relative Queue demand.
 */
@JsonTypeName("util")
class UtilizationBalanceStrategy implements ILoadBalancingStrategy {

    private Random rng = null;

    /**
     * Returns the instance of the given list, which currently has the lowest demand left.
     */
    @Override
    public MicroserviceInstance getNextInstance(Collection<MicroserviceInstance> runningInstances) {
        if (rng == null) {
            createRNG(runningInstances);
        }

        if (runningInstances.isEmpty()) {
            throw new NoInstanceAvailableException();
        }

        double min =
            runningInstances.stream().mapToDouble(MicroserviceInstance::getRelativeWorkDemand).min().orElse(-1);

        List<MicroserviceInstance> minimalInstances = runningInstances.stream()
            .filter(instance -> instance.getRelativeWorkDemand() == min)
            .collect(Collectors.toList());

        final int index = rng.nextInt(minimalInstances.size());
        final MicroserviceInstance microserviceInstance = minimalInstances.get(index);
        return microserviceInstance;
    }


    private void createRNG(Collection<MicroserviceInstance> runningInstances) {
        runningInstances.stream().findFirst().ifPresent(instance -> {
            MiSimModel model = (MiSimModel) instance.getModel();
            rng = new Random(model.getExperimentMetaData().getSeed());
        });
    }
}
