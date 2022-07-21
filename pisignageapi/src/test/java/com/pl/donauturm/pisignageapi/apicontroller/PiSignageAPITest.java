package com.pl.donauturm.pisignageapi.apicontroller;

import static org.junit.jupiter.api.Assertions.*;

import com.pl.donauturm.pisignageapi.model.Asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class PiSignageAPITest {

    private PiSignageAPI api;

    @BeforeEach
    void setUp() {
        api = new PiSignageAPI("philippletschka", "S4T2x9F@yEKYnA3");
    }

    @Test
    void login() {
        api.login();
        assertTrue(api.isLoggedIn());
        api.logout();
        assertFalse(api.isLoggedIn());
    }

    @Test
    void uploadAsset() {
        api.uploadAsset(new File("F:\\Code\\Donauturm\\app\\src\\main\\res\\drawable\\img_logo_corporate.jpg")).withLabels("generated").upload();
        List<Asset> assets = api.getAllAssets();
        assertTrue(assets.stream().map(Asset::getName).anyMatch(an -> an.equals("img_logo_corporate.jpg")));
        api.deleteAsset("img_logo_corporate.jpg");
    }
}
