package pl.wsiz.katering.Callback;

import java.util.List;

import pl.wsiz.katering.Model.CommentModel;

public interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}
