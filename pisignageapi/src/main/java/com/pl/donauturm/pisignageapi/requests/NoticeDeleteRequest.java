package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.universal.StringResponseMessage;

public class NoticeDeleteRequest extends Request<String, StringResponseMessage> {

    public String filename;

    public NoticeDeleteRequest(String filename) {
        super(null);
        this.filename = filename;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.DELETE;
    }

    @Override
    protected String requestPath() {
        return "notices/" + filename;
    }

    @Override
    protected Class<? extends StringResponseMessage> provideResultClass(String body) {
        return StringResponseMessage.class;
    }
}

