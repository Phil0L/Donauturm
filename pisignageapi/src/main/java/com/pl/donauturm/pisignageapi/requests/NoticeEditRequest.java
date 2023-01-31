package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.messages.NoticeUploadMessage;
import com.pl.donauturm.pisignageapi.model.universal.StringResponseMessage;

public class NoticeEditRequest extends Request<NoticeUploadMessage, StringResponseMessage>{

    private final String noticeName;

    public NoticeEditRequest(String noticeName, NoticeUploadMessage requestMessage) {
        super(requestMessage);
        this.noticeName = noticeName;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected String requestPath() {
        return "notices/" + noticeName;
    }

    @Override
    protected Class<? extends StringResponseMessage> provideResultClass(String body) {
        return StringResponseMessage.class;
    }
}
