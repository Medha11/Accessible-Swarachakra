package game.Typing;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class CustomArrayAdapter extends BaseAdapter
{
    private Context context;
    private String[] testList;
    private int selectedIndex;
    private int selectedColor = Color.parseColor("#1b1b1b");

    public CustomArrayAdapter(Context ctx, String[] testList)
    {
        this.context = ctx;
        this.testList = testList;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int ind)
    {
        selectedIndex = ind;
        notifyDataSetChanged();
    }
    public int getSelectedIndex() {
		return selectedIndex;
	}

    @Override
    public int getCount()
    {
        return testList.length;
    }

    @Override
    public Object getItem(int position)
    {
        return testList[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolder
    {
        TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        ViewHolder holder;
        if(convertView == null)
        {
            vi = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            holder = new ViewHolder();

            holder.tv = (TextView) vi;

            vi.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) vi.getTag();
        }

        if(selectedIndex!= -1 && position == selectedIndex)
        {
            holder.tv.setBackground(context.getResources().getDrawable(R.drawable.listitemhighlight));
        }
        else
        {
            //holder.tv.setBackground(null);
            holder.tv.setBackground(context.getResources().getDrawable(R.drawable.border));
        }
        
        //holder.tv.setText("" + (position + 1) + " " + testList.get(position).getTestText());
        holder.tv.setText(testList[position]);

        return vi;
    }

	

}