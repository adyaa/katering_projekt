package pl.wsiz.katering.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.wsiz.katering.Database.CartItem;
import pl.wsiz.katering.EventBus.UpdateItemInCart;
import pl.wsiz.katering.R;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage())
                .into(holder.img_cart);
        holder.txt_food_name.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
        holder.txt_food_price.setText(new StringBuilder("")
        .append(cartItemList.get(position).getFoodPrice() + cartItemList.get(position).getFoodExtraPrice()));

        holder.number_button.setNumber(String.valueOf(cartItemList.get(position).getFoodQuantity()));

        //start eventu
        holder.number_button.setOnValueChangeListener((view, oldValue, newValue) -> {
            //po naciśnięciu tego przycisku baza danych zaktualizuje się o daną ilość
            cartItemList.get(position).setFoodQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }


    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Unbinder unbinder;

        @BindView(R.id.img_cart)
        ImageView img_cart;

        @BindView(R.id.txt_food_price)
        TextView txt_food_price;

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.number_button)
        ElegantNumberButton number_button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
