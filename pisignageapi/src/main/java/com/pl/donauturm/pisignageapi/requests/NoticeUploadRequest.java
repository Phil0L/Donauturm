package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.files.data.NoticeData;

public class NoticeUploadRequest extends Request<NoticeData, Void>{

    public NoticeUploadRequest(NoticeData requestMessage) {
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
    protected Class<? extends Void> provideResultClass(String body) {
        return null;
    }
}
