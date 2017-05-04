package game.Typing;

public class RowItem {
	private int imageId;
	private String title;
	//private String desc;
	private int next;
	
	public RowItem(int imageId, String title, int next) {
		this.imageId = imageId;
		this.title = title;
		//this.desc = desc;
		this.next = next;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public int getNext() {
		return next;
	}
	public void setDesc(int next) {
		this.next = next;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/*	@Override
	public String toString() {
		return title + "\n" + desc;
	}*/	
}