package se.fusion1013.cobaltmagick.alchemy.transmutation.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.Response;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.Transmutation;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.TransmutationLocation;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.TransmutationState;
import se.fusion1013.cobaltmagick.alchemy.transmutation.repository.ITransmutationRepository;
import se.fusion1013.cobaltmagick.alchemy.transmutation.task.TransmutationLocationDisplayTickTask;
import se.fusion1013.cobaltmagick.alchemy.transmutation.task.TransmutationLocationTickTask;

import java.util.ArrayList;
import java.util.List;

public class TransmutationService extends Manager<CobaltMagick> {

    private static final ITransmutationRepository repository = DataManager.getInstance().getDao(ITransmutationRepository.class);
    private static final List<TransmutationLocation> TRANSMUTATIONS = new ArrayList<>();

    public TransmutationService(CobaltMagick plugin) {
        super(plugin);
    }

    public Response addTransmutation(String input, String output, String catalyst, int cost) {
        Transmutation transmutation = new Transmutation();
        transmutation.setInputItem(input);
        transmutation.setOutputItem(output);
        transmutation.setCatalyst(catalyst);
        transmutation.setCost(cost);
        repository.addTransmutation(transmutation);
        return Response.ok("Added new transmutation");
    }

    public Response createTransmutationLocation(Location location) {
        TransmutationLocation transmutationLocation = new TransmutationLocation(location);
        TRANSMUTATIONS.add(transmutationLocation);
        return Response.ok("Created new transmutation location");
    }

    public List<Transmutation> getTransmutations() {
        return repository.getTransmutations();
    }

    public void tickTransmutations() {
        for (int i = TRANSMUTATIONS.size() - 1; i >= 0; i--) {
            TransmutationLocation transmutation = TRANSMUTATIONS.get(i);
            transmutation.tick();
            if (transmutation.getState() != TransmutationState.DONE) continue;
            TRANSMUTATIONS.remove(i);
        }
    }

    public void tickTransmutationsDisplay() {
        for (int i = TRANSMUTATIONS.size() - 1; i >= 0; i--) {
            TransmutationLocation transmutation = TRANSMUTATIONS.get(i);
            transmutation.displayTick();
            if (transmutation.getState() != TransmutationState.DONE) continue;
            TRANSMUTATIONS.remove(i);
        }
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), new TransmutationLocationTickTask(), 1, 20);
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), new TransmutationLocationDisplayTickTask(), 1, 1);
    }

    @Override
    public void disable() {

    }

    private static TransmutationService INSTANCE;

    public static TransmutationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransmutationService(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
