package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;
import com.pl.donauturm.pisignageapi.model.universal.StringResponseMessage;

public class NoticeUploadRequest extends Request<NoticeUploadMessage, StringResponseMessage>{

    public NoticeUploadRequest(NoticeUploadMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected String requestPath() {
        return "notices";
    }

    @Override
    protected Class<? extends StringResponseMessage> provideResultClass(String body) {
        return StringResponseMessage.class;
    }
}