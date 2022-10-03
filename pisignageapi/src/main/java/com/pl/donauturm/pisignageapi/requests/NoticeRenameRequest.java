package com.pl.donauturm.pisignageapi.requests;

import com.pl.donauturm.pisignageapi.model.universal.StringResponseMessage;

public class NoticeRenameRequest extends Request<NoticeRenameRequest.NoticeRenameMessage, StringResponseMessage> {

    public String filename;

    public NoticeRenameRequest(String filename, String newFilename) {
        super(new NoticeRenameMessage(newFilename));
        this.filename = filename;
    }

    @Override
    protected RequestMethod requestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected String requestPath() {
        return "notices/" + filename;
    }

    @Override
    protected Class<? extends StringResponseMessage> provideResultClass(String body) {
        return StringResponseMessage.class;
    }

    public static class NoticeRenameMessage {
        public String newname;

        public NoticeRenameMessage(String newFilename) {
            this.newname = newFilename;
        }
    }
}

