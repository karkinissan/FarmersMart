package com.example.nissan.farmersmart;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ProductAdapter extends ArrayAdapter<Product> {
    private Context context;
    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.thumbnail);
        TextView nameTextView = convertView.findViewById(R.id.productName);
        TextView locationTextView = convertView.findViewById(R.id.location);
        TextView quantityTextView = convertView.findViewById(R.id.quantity);
        TextView priceTextView = convertView.findViewById(R.id.price);
        TextView dateExpiryTextView = convertView.findViewById(R.id.dateExpiry);

        Product product = getItem(position);

        /*Glide.with(photoImageView.getContext())
                .load(product.getPhotoUrl())
                .into(photoImageView);*/
//        System.out.println(product.printAll());
        Log.v("ProductAdapter",product.getName().toLowerCase());
        nameTextView.setText(product.getName());
        locationTextView.setText(product.getLocation());
        quantityTextView.setText(R.string.list_item_quantity);
        quantityTextView.append(String.format(Locale.getDefault(),"%d",product.getQuantity()));
        String weightUnit = product.getWeightUnit();

        String weightUnitLoc;
        if (weightUnit.equals("Kg")){
            weightUnitLoc = getContext().getString(R.string.kg);
        }
        else {
            weightUnitLoc = getContext().getString(R.string.tonne);
        }
        quantityTextView.append(" " + weightUnitLoc);

        priceTextView.setText(R.string.list_item_price);
        priceTextView.append(String.format(Locale.getDefault(),"%.2f",product.getRate()));
        String expiryDate = product.getDateExpiry();
        String formattedExpiryDate = formatExpiryDate(expiryDate);
        dateExpiryTextView.setText(formattedExpiryDate);

        switch (product.getName()) {
            case "Apple":
                photoImageView.setImageResource(R.drawable.apples);
                break;
            case "Banana":
                photoImageView.setImageResource(R.drawable.bananas);
                break;
            case "Cauliflower":
                photoImageView.setImageResource(R.drawable.cauliflower);
                break;
            case "Potatoes":
                photoImageView.setImageResource(R.drawable.potatoes);
                break;
            case "Tomatoes":
                photoImageView.setImageResource(R.drawable.tomatoes);
                break;
            default:
                photoImageView.setImageResource(R.drawable.fruitsandvegetables);
                break;
        }

        return convertView;
    }

    private String formatExpiryDate(String expiryDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        try {
            calendar.setTime(inFormat.parse(expiryDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault());
        return outFormat.format(calendar.getTime());
//        String formattedExpiryDate = expiryDate.substring(0,4);
//        formattedExpiryDate.concat("/"+expiryDate.substring(4,6));
//        formattedExpiryDate.concat("/"+expiryDate.substring(6,8));
//        formattedExpiryDate.concat(" "+expiryDate.substring(8,10));
//        formattedExpiryDate.concat(":"+expiryDate.substring(10));
//        return formattedExpiryDate;
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        Log.v("ProductAdapter",""+resId);
        if (resId == 0) {
            return aString;
        } else {
            return context.getString(resId);
        }
    }
}
