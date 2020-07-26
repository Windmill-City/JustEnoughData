package city.windmill.util;

public class Rect {
    public double left;
    public double top;
    public double width;
    public double height;
    public Rect(){}
    public Rect(double left, double top, double width, double height){
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
    public Rect(Rect rect){
        this(rect.left, rect.top, rect.width, rect.height);
    }
    public Rect(Vec2d lt, Vec2d rb){
        this(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
    }

    public void apply(double left, double top, double width, double height){
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public void apply(Vec2d lt, Vec2d rb){
        apply(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
    }
    //region get pos
    public Vec2d getCenterPos(){
        Vec2d pos = new Vec2d(left,top);
        pos.x += width / 2D;
        pos.y += height / 2D;
        return pos;
    }

    public Vec2d getLTPos(){
        Vec2d pos = new Vec2d(left,top);
        return pos;
    }

    public Vec2d getLBPos(){
        Vec2d pos = new Vec2d(left,top);
        pos.y += height;
        return pos;
    }

    public Vec2d getRTPos(){
        Vec2d pos = new Vec2d(left,top);
        pos.x += width;
        return pos;
    }

    public Vec2d getRBPos(){
        Vec2d pos = new Vec2d(left,top);
        pos.x += width;
        pos.y += height;
        return pos;
    }
    //endregion
}
