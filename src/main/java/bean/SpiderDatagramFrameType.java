package bean;

/**
 * @author LEGEND
 */

public enum SpiderDatagramFrameType {
    /**
     * 身份识别
     */
    IDENTIFICATION((byte) 1),
    FIRST_FRAME((byte) 2),
    MIDDLE_FRAME((byte) 3),
    LAST_FRAME((byte) 4),
    UNKNOWN((byte) -1);
    private final byte value;

    SpiderDatagramFrameType(byte value) {
        this.value = value;
    }

    public static SpiderDatagramFrameType getInstance(byte value) {
        switch (value) {
            case 1:
                return IDENTIFICATION;
            case 2:
                return FIRST_FRAME;
            case 3:
                return MIDDLE_FRAME;
            case 4:
                return LAST_FRAME;
            default:
                return UNKNOWN;
        }

    }

    public byte getValue() {
        return value;
    }
}
