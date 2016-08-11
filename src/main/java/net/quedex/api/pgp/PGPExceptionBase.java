package net.quedex.api.pgp;

public class PGPExceptionBase extends Exception {

    PGPExceptionBase() {
    }

    PGPExceptionBase(String message) {
        super(message);
    }

    PGPExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }

    PGPExceptionBase(Throwable cause) {
        super(cause);
    }
}
