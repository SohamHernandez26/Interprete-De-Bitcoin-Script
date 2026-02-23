package edu.uvg;

public class StackItem {

    private final byte[] data;

    public StackItem(byte[] data) {
        this.data = data;
    }

    public boolean asBoolean() {
        for (byte b : data) {
            if (b != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(StackItem otherItem) {
        if (this.data.length != otherItem.data.length) {
            return false;
        }
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] != otherItem.data[i]) {
                return false;
            }
        }
        return true;
    }
    
}
