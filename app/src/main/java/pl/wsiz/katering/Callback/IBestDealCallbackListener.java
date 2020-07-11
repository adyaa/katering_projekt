package pl.wsiz.katering.Callback;

import java.util.List;

import pl.wsiz.katering.Model.BestDealModel;
import pl.wsiz.katering.Model.PopularCategoryModel;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}
