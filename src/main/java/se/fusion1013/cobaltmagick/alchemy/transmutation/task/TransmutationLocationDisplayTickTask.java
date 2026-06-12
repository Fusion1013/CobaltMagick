package se.fusion1013.cobaltmagick.alchemy.transmutation.task;

import se.fusion1013.cobaltmagick.alchemy.transmutation.service.TransmutationService;

public class TransmutationLocationDisplayTickTask implements Runnable {

    private static final TransmutationService service = TransmutationService.getInstance();

    @Override
    public void run() {
        service.tickTransmutationsDisplay();
    }

}
