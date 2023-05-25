package JavaPlugin;

import javax.swing.table.AbstractTableModel;

/**
 * @author R1co1a
 *
 */
public class ValueLoopTableModel extends AbstractTableModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5815523906566848273L;
	
	private final String[] header = { "No.", "Loop Description", "Score"};
	
	@Override
	public int getRowCount() {

		return 0;
	}

	@Override
	public int getColumnCount() {

		return this.header.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		return null;
	}
	@Override
	public String getColumnName(int columnIndex) {
		return this.header[columnIndex];
	}

}
