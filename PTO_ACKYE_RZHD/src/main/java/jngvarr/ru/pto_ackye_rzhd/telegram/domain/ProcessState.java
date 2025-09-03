package jngvarr.ru.pto_ackye_rzhd.telegram.domain;

public enum ProcessState {
    WAITING_FOR_METER_PHOTO,
    WAITING_FOR_DC_PHOTO,
    WAITING_FOR_TT_PHOTO,
    MANUAL_INSERT_METER_NUMBER,
    MANUAL_INSERT_METER_INDICATION,
    IIK_WORKS,
    DC_WORKS,
    IIK_MOUNT,
    DC_MOUNT,
    REGISTRATION
}
