package game.Typing;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

	Context context;
	private int selectedIndex;

	public CustomListViewAdapter(Context context, int resourceId,
			List<RowItem> items) {
		super(context, resourceId, items);
		this.context = context;
		selectedIndex = -1;
	} 
	
	/*private view holder class*/
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
		//TextView txtDesc;
		ImageView next;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		
		this.selectedIndex = selectedIndex;
		notifyDataSetChanged();
		
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem rowItem = getItem(position);
		
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.next = (ImageView) convertView.findViewById(R.id.user_select);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else 
			holder = (ViewHolder) convertView.getTag();
		
		if(selectedIndex!= -1 && position == selectedIndex)
        {
            //holder.txtTitle.setBackground(null);
            //holder.next.setBackground(null);
            //holder.imageView.setBackground(null);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.bluelistview));
        }
        else
        {
        	/*holder.txtTitle.setBackgroundColor(context.getResources().getColor(R.color.grey02));
        	holder.next.setBackgroundColor(context.getResources().getColor(R.color.grey02));
        	holder.imageView.setBackgroundColor(context.getResources().getColor(R.color.grey02));*/
        	convertView.setBackground(null);
        }
		
		holder.next.setImageResource(rowItem.getNext());
		holder.txtTitle.setText(rowItem.getTitle());
		holder.imageView.setImageResource(rowItem.getImageId());
		
		return convertView;
	}
}