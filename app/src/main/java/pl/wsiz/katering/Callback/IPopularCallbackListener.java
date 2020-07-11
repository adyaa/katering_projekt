package pl.wsiz.katering.Callback;

import java.util.List;

import pl.wsiz.katering.Model.PopularCategoryModel;

public interface IPopularCallbackListener {

    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}
