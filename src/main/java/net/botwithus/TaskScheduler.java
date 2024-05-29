package net.botwithus;

public class TaskScheduler {
    public int amountToDisassemble;
    public String itemToDisassemble;
    private int amountDisassembled = 0;

    public TaskScheduler(int amountToDisassemble, String itemToDisassemble) {
        this.amountToDisassemble = amountToDisassemble;
        this.itemToDisassemble = itemToDisassemble;
    }

    public boolean isComplete() {
        return amountDisassembled >= amountToDisassemble;
    }

    public void incrementAmountDisassembled() {
        amountDisassembled++;
    }

    public int getAmountDisassembled() {
        return amountDisassembled;
    }
    public String getItemToDisassemble() {
        return itemToDisassemble;
    }
}
