package city.windmill.util;

public class Vec2d {
    public static final Vec2d ZERO = new Vec2d(0D, 0D);
    public static final Vec2d ONE = new Vec2d(1D, 1D);
    public static final Vec2d UNIT_X = new Vec2d(-1D, 0D);
    public static final Vec2d NEGATIVE_UNIT_X = new Vec2d(-1D, 0D);
    public static final Vec2d UNIT_Y = new Vec2d(0D, 1D);
    public static final Vec2d NEGATIVE_UNIT_Y = new Vec2d(0D, -1D);
    public static final Vec2d MAX = new Vec2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Vec2d MIN = new Vec2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

    public double x;
    public double y;
    public Vec2d(){}
    public Vec2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2d var){
        this.x = var.x;
        this.y = var.y;
    }

    public Vec2d apply(Vec2d var){
        x = var.x;
        y = var.y;
        return this;
    }
    public Vec2d scale(double scale){
        x *= scale;
        y *= scale;
        return this;
    }
    public Vec2d minus(Vec2d vec){
        x -= vec.x;
        y -= vec.y;
        return this;
    }
    public Vec2d plus(Vec2d vec){
        x += vec.x;
        y += vec.y;
        return this;
    }
    //region min max
    public Vec2d max_X(Vec2d var1){
        x = Math.max(var1.x, this.x);
        return this;
    }
    
    public Vec2d max_Y(Vec2d var1){
        y = Math.max(var1.y, this.y);
        return this;
    }
    public Vec2d max_XY(Vec2d var1){
        max_X(var1);
        max_Y(var1);
        return this;
    }
    public Vec2d min_X(Vec2d var1){
        x = Math.min(var1.x, this.x);
        return this;
    }

    public Vec2d min_Y(Vec2d var1){
        y = Math.min(var1.y, this.y);
        return this;
    }
    public Vec2d min_XY(Vec2d var1){
        min_X(var1);
        min_Y(var1);
        return this;
    }
    //endregion
    //region object methods
    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof Vec2d && ((Vec2d)obj).x == this.x && ((Vec2d)obj).y == this.y);
    }

    @Override
    public Vec2d clone() {
        return new Vec2d(this);
    }
    //endregion
}
