package com.parkit.parkingsystem.integration;


import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {

    InputReaderUtil inputReaderUtil;

    private InputStream ScanIn;


    @Test
    public void readSelectionScanError () {
        String input = "&";
        ScanIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(ScanIn);
        inputReaderUtil = new InputReaderUtil();
        assertEquals(-1, inputReaderUtil.readSelection());
    }
    @Test
    public void readVehicleRegistrationNumberOk () throws Exception {
        String input = "ABCDEF";
        ScanIn = new ByteArrayInputStream(input.getBytes());
        System.setIn(ScanIn);
        inputReaderUtil = new InputReaderUtil();
        assertEquals("ABCDEF", inputReaderUtil.readVehicleRegistrationNumber());
    }

}
