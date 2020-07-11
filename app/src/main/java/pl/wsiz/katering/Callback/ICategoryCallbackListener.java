package pl.wsiz.katering.Callback;

import java.util.List;

import pl.wsiz.katering.Model.CategoryModel;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModelList);
    void onCategoryLoadFailed(String message);
}
