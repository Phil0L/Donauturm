package com.pl.donauturm.pisignageapi.model.session.data;

public class UserSettings {

  public UserZoneAliases zoneAliases;
  public UserUIDefaults uiDefaults;
  public boolean signupForBeta;
  public boolean serveOldUi;
  public boolean newLayoutsEnable;
  public boolean systemMassagesHide;
  public boolean forceTvOn;
  public boolean disableCECPowerCheck;
  public boolean hideWelcomeNotice;
  public int defaultDuration;
  public String language;
  public String sshPassword;
  public boolean enableLog;
  public boolean subscribeForAlerts;
  public int reportIntervalMinutes;
  public boolean enableYoutubeDl;
  public boolean licenseOnly;
  public boolean shareableLabels;
  public boolean playerAutoRegistration;
  public boolean disableDownload;
  public boolean disablePlayerDownloadOnModem;

  public UserSettings(UserZoneAliases zoneAliases, UserUIDefaults uiDefaults, boolean signupForBeta, boolean serveOldUi, boolean newLayoutsEnable, boolean systemMassagesHide, boolean forceTvOn, boolean disableCECPowerCheck, boolean hideWelcomeNotice, int defaultDuration, String language, String sshPassword, boolean enableLog, boolean subscribeForAlerts, int reportIntervalMinutes, boolean enableYoutubeDl, boolean licenseOnly, boolean shareableLabels, boolean playerAutoRegistration, boolean disableDownload, boolean disablePlayerDownloadOnModem) {
    this.zoneAliases = zoneAliases;
    this.uiDefaults = uiDefaults;
    this.signupForBeta = signupForBeta;
    this.serveOldUi = serveOldUi;
    this.newLayoutsEnable = newLayoutsEnable;
    this.systemMassagesHide = systemMassagesHide;
    this.forceTvOn = forceTvOn;
    this.disableCECPowerCheck = disableCECPowerCheck;
    this.hideWelcomeNotice = hideWelcomeNotice;
    this.defaultDuration = defaultDuration;
    this.language = language;
    this.sshPassword = sshPassword;
    this.enableLog = enableLog;
    this.subscribeForAlerts = subscribeForAlerts;
    this.reportIntervalMinutes = reportIntervalMinutes;
    this.enableYoutubeDl = enableYoutubeDl;
    this.licenseOnly = licenseOnly;
    this.shareableLabels = shareableLabels;
    this.playerAutoRegistration = playerAutoRegistration;
    this.disableDownload = disableDownload;
    this.disablePlayerDownloadOnModem = disablePlayerDownloadOnModem;
  }

  public UserSettings() {
  }

  public UserZoneAliases getZoneAliases() {
    return zoneAliases;
  }

  public UserUIDefaults getUiDefaults() {
    return uiDefaults;
  }

  public boolean isSignupForBeta() {
    return signupForBeta;
  }

  public boolean isServeOldUi() {
    return serveOldUi;
  }

  public boolean isNewLayoutsEnable() {
    return newLayoutsEnable;
  }

  public boolean isSystemMassagesHide() {
    return systemMassagesHide;
  }

  public boolean isForceTvOn() {
    return forceTvOn;
  }

  public boolean isDisableCECPowerCheck() {
    return disableCECPowerCheck;
  }

  public boolean isHideWelcomeNotice() {
    return hideWelcomeNotice;
  }

  public int getDefaultDuration() {
    return defaultDuration;
  }

  public String getLanguage() {
    return language;
  }

  public String getSshPassword() {
    return sshPassword;
  }

  public boolean isEnableLog() {
    return enableLog;
  }

  public boolean isSubscribeForAlerts() {
    return subscribeForAlerts;
  }

  public int getReportIntervalMinutes() {
    return reportIntervalMinutes;
  }

  public boolean isEnableYoutubeDl() {
    return enableYoutubeDl;
  }

  public boolean isLicenseOnly() {
    return licenseOnly;
  }

  public boolean isShareableLabels() {
    return shareableLabels;
  }

  public boolean isPlayerAutoRegistration() {
    return playerAutoRegistration;
  }

  public boolean isDisableDownload() {
    return disableDownload;
  }

  public boolean isDisablePlayerDownloadOnModem() {
    return disablePlayerDownloadOnModem;
  }

  @Override
  public String toString() {
    return "UserSettings{" +
           "zoneAliases=" + zoneAliases +
           ", uiDefaults=" + uiDefaults +
           ", signupForBeta=" + signupForBeta +
           ", serveOldUi=" + serveOldUi +
           ", newLayoutsEnable=" + newLayoutsEnable +
           ", systemMassagesHide=" + systemMassagesHide +
           ", forceTvOn=" + forceTvOn +
           ", disableCECPowerCheck=" + disableCECPowerCheck +
           ", hideWelcomeNotice=" + hideWelcomeNotice +
           ", defaultDuration=" + defaultDuration +
           ", language='" + language + '\'' +
           ", sshPassword='" + sshPassword + '\'' +
           ", enableLog=" + enableLog +
           ", subscribeForAlerts=" + subscribeForAlerts +
           ", reportIntervalMinutes=" + reportIntervalMinutes +
           ", enableYoutubeDl=" + enableYoutubeDl +
           ", licenseOnly=" + licenseOnly +
           ", shareableLabels=" + shareableLabels +
           ", playerAutoRegistration=" + playerAutoRegistration +
           ", disableDownload=" + disableDownload +
           ", disablePlayerDownloadOnModem=" + disablePlayerDownloadOnModem +
           '}';
  }
}
