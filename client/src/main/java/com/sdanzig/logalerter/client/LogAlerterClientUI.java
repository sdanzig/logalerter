package com.sdanzig.logalerter.client;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdanzig.logalerter.common.dto.Alert;
import com.sdanzig.logalerter.common.dto.Log;

@Component
public class LogAlerterClientUI extends JFrame implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LogAlerterClientUI.class);

	@Autowired
	private LogAlerterClientController controller;

	JTable jt;
	AlertsTableModel tableModel;
	int lastHashCodeProcessed = 0;
	int latestHashCode = 0;
	private String serverUiUrl;

	public LogAlerterClientUI() {
		initUI();
	}

	private void initUI() {
		tableModel = new AlertsTableModel();
		jt=new JTable(tableModel);
		jt.setBounds(30,40,200,300);
		JScrollPane sp=new JScrollPane(jt);
		add(sp);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
							  @Override
							  public void windowClosing(WindowEvent windowEvent) {
								  controller.shutdownThread();
							  }
						  });

		setTitle("LogAlerter Client");
		setSize(800, 600);
		setLocationRelativeTo(null);
		jt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = jt.rowAtPoint(new Point(e.getX(), e.getY()));
				int col = jt.columnAtPoint(new Point(e.getX(), e.getY()));
				log.debug(row + " " + col);

				String value = (String) jt.getModel().getValueAt(row, col);

				log.debug(value + " was clicked");
				String[] parts = value.toString().split("\\|");
				String urlString = serverUiUrl+"alert-details/"+parts[0];
				try {
					URL url = new URL(urlString);
					openWebpage(url);
				}
				catch (MalformedURLException malformedURLException) {
					log.error("Unable to open malformed URL \""+urlString+"\"");
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				int col = jt.columnAtPoint(new Point(e.getX(), e.getY()));
				if (col == 0) {
					jt.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				int col = jt.columnAtPoint(new Point(e.getX(), e.getY()));
				if (col != 0) {
					jt.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
/*
		jt.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {

			@Override
			public java.awt.Component getTableCellRendererComponent(JTable table, final Object value, boolean arg2,
					boolean arg3, int arg4, int arg5) {
				String[] parts = value.toString().split("\\|");
				final JLabel lab = new JLabel("<html><a href=\"" + serverUiUrl+"alerts/"+parts[0] + "\">" + parts[1] + "</a></html>");
				return lab;
			}
		});
 */
		for (int i = 0; i < jt.getColumnCount(); i++) {
			jt.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {

				@Override
				public java.awt.Component getTableCellRendererComponent(JTable table, final Object value,
						boolean isSelected, boolean hasFocus, int row, int col) {
					java.awt.Component toRender;
					if (col == 1) {
						String[] parts = value.toString().split("\\|");
						JLabel label = new JLabel(
								"<html><a href=\"" + serverUiUrl + "alerts/" + parts[0] + "\">" + parts[1]
										+ "</a></html>");
						label.setOpaque(true);
						toRender = label;
					}
					else {
						toRender = super.getTableCellRendererComponent(table, value,
								isSelected, hasFocus, row, col);
					}
					Alert alertForRow = fullListOfAlerts.get(row);
					switch ((int) alertForRow.getSeverity()) {
					case Alert.ALERT_IGNORABLE:
						toRender.setBackground(Color.WHITE);
						break;
					case Alert.ALERT_WARNING:
						toRender.setBackground(Color.YELLOW);
						break;
					case Alert.ALERT_CRITICAL:
						toRender.setBackground(Color.RED);
						break;
					}
					return toRender;
				}
			});
		}
	}

	class AlertsTableModel extends AbstractTableModel {
		String column[]={"Log name","Label","# seen", "Last seen"};

		@Override
		public String getColumnName(int index) {
			return column[index];
		}

		@Override
		public int getRowCount() {
			return fullListOfAlerts.size();
		}

		@Override
		public int getColumnCount() {
			return column.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(rowIndex < fullListOfAlerts.size()) {
				Alert alert = fullListOfAlerts.get(rowIndex);
				switch (columnIndex) {
				case 0:
					long logId = alert.getLogId();
					Log logObj = logInfo.get(logId);
					String logDesc = logObj != null ? logObj.getDescription() : logId + "";
					return logDesc;
				case 1:
					return alert.getId() + "|" + alert.getLabel();
				case 2:
					return alert.getOccurrences() + "";
				case 3:
					return alert.getLastOccurrence() + "";
				}
			}
			return null;
		}
	}

	public static boolean openWebpage(URL url) {
		URI uri = null;
		try {
			uri = url.toURI();
		}
		catch (URISyntaxException e) {
			log.error("Invalid URL specified for fetching alert details.", e);
			return false;
		}
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				log.error("Could not open browser to display alert details.", e);
			}
		} else {
			log.error("Desktop browser not supported.");
		}
		return false;
	}

	public void activateClientUi() {
		startDataRetrievalThread();
	}

	@Override
	public void run() {
		while(true) {
			updateStoredAlertsList();
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				log.warn("Data retrieval thread interrupted.", e);
				System.exit(0);
			}
		}
	}

	private void startDataRetrievalThread() {
		Thread dataRetrievalThread = new Thread(this);
		dataRetrievalThread.start();
	}

	private List<Alert> fullListOfAlerts = new ArrayList<>();
	private volatile Map<Long, Log> logInfo;

	private void updateStoredAlertsList() {
		logInfo = controller.getLogInfo();
		fullListOfAlerts = new ArrayList<>();
		for(Long logId : logInfo.keySet()) {
			List<Alert> alerts = controller.getAlertsForLog(logId);
			fullListOfAlerts.addAll(alerts);
		}
		Collections.sort(fullListOfAlerts, new AlertsSort());
		latestHashCode = fullListOfAlerts.hashCode();
		tableModel.fireTableDataChanged();
	}

	public void setServerUiUrl(String serverUiUrl) {
		this.serverUiUrl = serverUiUrl;
	}

	private class AlertsSort implements Comparator<Alert>
	{
		public int compare(Alert a, Alert b)
		{
			Date aDate = a.getLastOccurrence();
			Date bDate = b.getLastOccurrence();
			if(aDate != null && bDate != null) {
				int compVal = -a.getLastOccurrence().compareTo(b.getLastOccurrence());
				if(compVal == 0) { // If occurrence time is equal, sort by unique alert IDs
					return Long.valueOf(a.getId()).compareTo(b.getId());
				}
				return compVal;
			} else if(aDate == null && bDate == null) {
				return Long.valueOf(a.getId()).compareTo(b.getId());
			} else if(aDate == null) {
				return 1; // bDate has a value, so it comes first
			}
			return -1; // aDate has a value, so it comes first
		}
	}
}
