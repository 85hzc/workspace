package su.rss.view;


/**
 * 鍒楄〃鎷栧姩鍚庡洖璋冩柟娉�
 * @author ch
 *
 */
public interface MoveListener {

	public void moveTo(int index, int direction);
	public void cancelMove(int index);
	
}
