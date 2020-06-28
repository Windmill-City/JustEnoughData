package city.windmill.Plan;

public class Amount {
    public int amount;
    public float chance;
    public Amount(int amount, float chance){
        this.amount = amount;
        this.chance = chance;
    }
    public Amount(int amount){
        this(amount, 1);
    }
}
