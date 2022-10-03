package com.pl.donauturm.pisignageapi.model.files.messages;

import com.pl.donauturm.pisignageapi.model.files.data.NoticeInfo;
import com.pl.donauturm.pisignageapi.model.universal.ResponseMessage;

public class NoticeInfoMessage implements ResponseMessage<NoticeInfo> {

    public String stat_message;
    public NoticeInfo data;
    public boolean success;

    public NoticeInfoMessage(String stat_message, NoticeInfo data, boolean success) {
        this.stat_message = stat_message;
        this.data = data;
        this.success = success;
    }

    public NoticeInfoMessage() {
    }

    @Override
    public String getStatusMessage() {
        return stat_message;
    }

    @Override
    public NoticeInfo getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "NoticeInfoMessage{" +
                "stat_message='" + stat_message + '\'' +
                ", data=" + data +
                ", success=" + success +
                '}';
    }
}
