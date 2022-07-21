package com.pl.donauturm.pisignageapi.model.groups.data;

import com.pl.donauturm.pisignageapi.model.Group;
import com.pl.donauturm.pisignageapi.model.GroupPlaylist;
import com.pl.donauturm.pisignageapi.model.files.data.FileValidity;
import com.pl.donauturm.pisignageapi.model.universal.Creator;
import com.pl.donauturm.pisignageapi.model.universal.Enable;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GroupData implements Group {

  public Enable sleep; // not fully
  public Enable reboot; // not fully
  public Enable kioskUi; // idk
  public Enable showClock; // not fully
  public Object monitorArrangement;
  public Object emergencyMessage;
  public Creator createdBy;
  public List<GroupPlaylistData> playlists;
  public boolean combineDefaultPlaylist;
  public boolean playAllEligiblePlaylists;
  public boolean shuffleContent;
  public boolean alternateContent;
  public int timeToStopVideo;
  public List<String> assets;
  public List<FileValidity> assetsValidity;
  public List<Object> deployedPlaylists;
  public List<String> deployedAssets;
  public List<String> labels;
  public boolean deployEveryday;
  public boolean enableMpv;
  public String mpvAudioDelay;
  public String selectedVideoPlayer;
  public boolean disableWebUi;
  public boolean disableWarnings;
  public boolean enablePio;
  public boolean disableAp;
  public String installation;
  public String orientation;
  public boolean animationEnable;
  public String animationType;
  public boolean resizeAssets;
  public boolean videoKeepAspect;
  public boolean videoShowSubtitles;
  public boolean imageLetterboxed;
  public String signageBackgroundColor;
  public boolean urlReloadDisable;
  public boolean keepWeblinksInMemory;
  public boolean loadPlaylistOnCompletion;
  public String resolution;
  public int imxVolume;
  public Object logo;
  public int logox;
  public int logoy;
  public String _id;
  public String name;
  public String createdAt;
  public int __v;
  public String playlistToSchedule;
  public int lastDeployed;
  public String deployTime;
  public String everydayDeployTime;
  public Enable ticker; // not fully
  public Enable deployedTicker; // not fully

  public GroupData(Enable sleep, Enable reboot, Enable kioskUi, Enable showClock, Object monitorArrangement, Object emergencyMessage, Creator createdBy, List<GroupPlaylistData> playlists, boolean combineDefaultPlaylist, boolean playAllEligiblePlaylists, boolean shuffleContent, boolean alternateContent, int timeToStopVideo, List<String> assets, List<FileValidity> assetsValidity, List<Object> deployedPlaylists, List<String> deployedAssets, List<String> labels, boolean deployEveryday, boolean enableMpv, String mpvAudioDelay, String selectedVideoPlayer, boolean disableWebUi, boolean disableWarnings, boolean enablePio, boolean disableAp, String installation, String orientation, boolean animationEnable, String animationType, boolean resizeAssets, boolean videoKeepAspect, boolean videoShowSubtitles, boolean imageLetterboxed, String signageBackgroundColor, boolean urlReloadDisable, boolean keepWeblinksInMemory, boolean loadPlaylistOnCompletion, String resolution, int imxVolume, Object logo, int logox, int logoy, String _id, String name, String createdAt, int __v, String playlistToSchedule, int lastDeployed, String deployTime, String everydayDeployTime, Enable ticker, Enable deployedTicker) {
    this.sleep = sleep;
    this.reboot = reboot;
    this.kioskUi = kioskUi;
    this.showClock = showClock;
    this.monitorArrangement = monitorArrangement;
    this.emergencyMessage = emergencyMessage;
    this.createdBy = createdBy;
    this.playlists = playlists;
    this.combineDefaultPlaylist = combineDefaultPlaylist;
    this.playAllEligiblePlaylists = playAllEligiblePlaylists;
    this.shuffleContent = shuffleContent;
    this.alternateContent = alternateContent;
    this.timeToStopVideo = timeToStopVideo;
    this.assets = assets;
    this.assetsValidity = assetsValidity;
    this.deployedPlaylists = deployedPlaylists;
    this.deployedAssets = deployedAssets;
    this.labels = labels;
    this.deployEveryday = deployEveryday;
    this.enableMpv = enableMpv;
    this.mpvAudioDelay = mpvAudioDelay;
    this.selectedVideoPlayer = selectedVideoPlayer;
    this.disableWebUi = disableWebUi;
    this.disableWarnings = disableWarnings;
    this.enablePio = enablePio;
    this.disableAp = disableAp;
    this.installation = installation;
    this.orientation = orientation;
    this.animationEnable = animationEnable;
    this.animationType = animationType;
    this.resizeAssets = resizeAssets;
    this.videoKeepAspect = videoKeepAspect;
    this.videoShowSubtitles = videoShowSubtitles;
    this.imageLetterboxed = imageLetterboxed;
    this.signageBackgroundColor = signageBackgroundColor;
    this.urlReloadDisable = urlReloadDisable;
    this.keepWeblinksInMemory = keepWeblinksInMemory;
    this.loadPlaylistOnCompletion = loadPlaylistOnCompletion;
    this.resolution = resolution;
    this.imxVolume = imxVolume;
    this.logo = logo;
    this.logox = logox;
    this.logoy = logoy;
    this._id = _id;
    this.name = name;
    this.createdAt = createdAt;
    this.__v = __v;
    this.playlistToSchedule = playlistToSchedule;
    this.lastDeployed = lastDeployed;
    this.deployTime = deployTime;
    this.everydayDeployTime = everydayDeployTime;
    this.ticker = ticker;
    this.deployedTicker = deployedTicker;
  }

  public GroupData() {
  }

  public Enable getSleep() {
    return sleep;
  }

  public Enable getReboot() {
    return reboot;
  }

  public Enable getKioskUi() {
    return kioskUi;
  }

  public Enable getShowClock() {
    return showClock;
  }

  public Object getMonitorArrangement() {
    return monitorArrangement;
  }

  public Object getEmergencyMessage() {
    return emergencyMessage;
  }

  public Creator getCreatedBy() {
    return createdBy;
  }

  public List<GroupPlaylist> getPlaylists() {
    return playlists.stream().map(g -> (GroupPlaylist) g).collect(Collectors.toList());
  }

  public boolean isCombineDefaultPlaylist() {
    return combineDefaultPlaylist;
  }

  public boolean isPlayAllEligiblePlaylists() {
    return playAllEligiblePlaylists;
  }

  public boolean isShuffleContent() {
    return shuffleContent;
  }

  public boolean isAlternateContent() {
    return alternateContent;
  }

  public int getTimeToStopVideo() {
    return timeToStopVideo;
  }

  public List<String> getAssetNames() {
    return assets;
  }

  public List<FileValidity> getAssetsValidity() {
    return assetsValidity;
  }

  public List<Object> getDeployedPlaylists() {
    return deployedPlaylists;
  }

  public List<String> getDeployedAssets() {
    return deployedAssets;
  }

  public List<String> getLabels() {
    return labels;
  }

  public boolean isDeployEveryday() {
    return deployEveryday;
  }

  public boolean isEnableMpv() {
    return enableMpv;
  }

  public String getMpvAudioDelay() {
    return mpvAudioDelay;
  }

  public String getSelectedVideoPlayer() {
    return selectedVideoPlayer;
  }

  public boolean isDisableWebUi() {
    return disableWebUi;
  }

  public boolean isDisableWarnings() {
    return disableWarnings;
  }

  public boolean isEnablePio() {
    return enablePio;
  }

  public boolean isDisableAp() {
    return disableAp;
  }

  public String getInstallation() {
    return installation;
  }

  public String getOrientation() {
    return orientation;
  }

  public boolean isAnimationEnable() {
    return animationEnable;
  }

  public Object getAnimationType() {
    return animationType;
  }

  public boolean isResizeAssets() {
    return resizeAssets;
  }

  public boolean isVideoKeepAspect() {
    return videoKeepAspect;
  }

  public boolean isVideoShowSubtitles() {
    return videoShowSubtitles;
  }

  public boolean isImageLetterboxed() {
    return imageLetterboxed;
  }

  public String getSignageBackgroundColor() {
    return signageBackgroundColor;
  }

  public boolean isUrlReloadDisable() {
    return urlReloadDisable;
  }

  public boolean isKeepWeblinksInMemory() {
    return keepWeblinksInMemory;
  }

  public boolean isLoadPlaylistOnCompletion() {
    return loadPlaylistOnCompletion;
  }

  public String getResolution() {
    return resolution;
  }

  public int getImxVolume() {
    return imxVolume;
  }

  public Object getLogo() {
    return logo;
  }

  public int getLogox() {
    return logox;
  }

  public int getLogoy() {
    return logoy;
  }

  public String getID() {
    return _id;
  }

  public String getName() {
    return name;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public Date getCreationTime(){
    try {
      return DateFormat.getDateTimeInstance().parse(createdAt);
    } catch (ParseException parseException) {
      return null;
    }
  }

  public int getVersion() {
    return __v;
  }

  public String getPlaylistToSchedule() {
    return playlistToSchedule;
  }

  @Override
  public String toString() {
    return "GroupData{" +
           "sleep=" + sleep +
           ", reboot=" + reboot +
           ", kioskUi=" + kioskUi +
           ", showClock=" + showClock +
           ", monitorArrangement=" + monitorArrangement +
           ", emergencyMessage=" + emergencyMessage +
           ", createdBy=" + createdBy +
           ", playlists=" + playlists +
           ", combineDefaultPlaylist=" + combineDefaultPlaylist +
           ", playAllEligiblePlaylists=" + playAllEligiblePlaylists +
           ", shuffleContent=" + shuffleContent +
           ", alternateContent=" + alternateContent +
           ", timeToStopVideo=" + timeToStopVideo +
           ", assets=" + assets +
           ", assetsValidity=" + assetsValidity +
           ", deployedPlaylists=" + deployedPlaylists +
           ", deployedAssets=" + deployedAssets +
           ", labels=" + labels +
           ", deployEveryday=" + deployEveryday +
           ", enableMpv=" + enableMpv +
           ", mpvAudioDelay='" + mpvAudioDelay + '\'' +
           ", selectedVideoPlayer='" + selectedVideoPlayer + '\'' +
           ", disableWebUi=" + disableWebUi +
           ", disableWarnings=" + disableWarnings +
           ", enablePio=" + enablePio +
           ", disableAp=" + disableAp +
           ", installation='" + installation + '\'' +
           ", orientation='" + orientation + '\'' +
           ", animationEnable=" + animationEnable +
           ", animationType=" + animationType +
           ", resizeAssets=" + resizeAssets +
           ", videoKeepAspect=" + videoKeepAspect +
           ", videoShowSubtitles=" + videoShowSubtitles +
           ", imageLetterboxed=" + imageLetterboxed +
           ", signageBackgroundColor='" + signageBackgroundColor + '\'' +
           ", urlReloadDisable=" + urlReloadDisable +
           ", keepWeblinksInMemory=" + keepWeblinksInMemory +
           ", loadPlaylistOnCompletion=" + loadPlaylistOnCompletion +
           ", resolution='" + resolution + '\'' +
           ", imxVolume=" + imxVolume +
           ", logo=" + logo +
           ", logox=" + logox +
           ", logoy=" + logoy +
           ", _id='" + _id + '\'' +
           ", name='" + name + '\'' +
           ", createdAt='" + createdAt + '\'' +
           ", __v=" + __v +
           ", playlistToSchedule='" + playlistToSchedule + '\'' +
           '}';
  }
}
