package io.github.mosaic141688.dropzoe;

/**
 * Created by mosaic on 10/9/17.
 */

public class Flyer {
    private String name;
    private boolean enabled;
    private boolean locked;

    public Flyer(){}
    public Flyer(String name, boolean enabled, boolean locked){
        setEnabled(enabled);
        setName(name);
        setLocked(locked);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
