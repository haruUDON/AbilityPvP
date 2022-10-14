package haruudon.udon.magicstick.cooldown;

public class SecSlot {
    private int sec;
    private int slot;

    public SecSlot(int sec, int slot){
        this.sec = sec;
        this.slot = slot;
    }
    public int getSec() {
        return sec;
    }
    public int getSlot() {
        return slot;
    }
}
