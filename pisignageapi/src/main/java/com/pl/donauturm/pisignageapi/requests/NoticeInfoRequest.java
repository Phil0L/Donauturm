package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.NoticeInfoMessage;

public class NoticeInfoRequest extends Request<String, NoticeInfoMessage> {

    public String filename;

    public NoticeInfoRequest(String filename) {
        super(null);
        this.filename = filename;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.GET;
    }

    @Override
    protected String requestPath() {
        return "notices/" + filename;
    }

    @Override
    protected Class<? extends NoticeInfoMessage> provideResultClass(String body) {
        return NoticeInfoMessage.class;
    }
}

