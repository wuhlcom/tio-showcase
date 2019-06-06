package org.tio.examples.im.client;

import java.text.NumberFormat;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.examples.im.client.ui.JFrameMain;
import org.tio.examples.im.common.CommandStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.utils.ImUtils;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.SetWithLock;

/**
 *
 * @author tanyaowu
 *
 */
public class ImClientAioListener implements ClientAioListener {
	private static Logger log = LoggerFactory.getLogger(ImClientAioListener.class);

	private static ImPacket handshakeReqPacket = new ImPacket(Command.COMMAND_HANDSHAKE_REQ);

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年12月16日 下午5:52:06
	 *
	 */
	public static void main(String[] args) {
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2016年12月16日 下午5:52:06
	 *
	 */
	public ImClientAioListener() {
	}

	//	@Override
	//	public void onAfterReconnected(ChannelContext initChannelContext, boolean isConnected)
	//	{
	//		if (isConnected)
	//		{
	//			JFrameMain.isNeedUpdateList = true;
	//			JFrameMain.isNeedUpdateConnectionCount = true;
	//		}
	//	}

	

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		JFrameMain jFrameMain = JFrameMain.getInstance();

		ImSessionContext imSessionContext = new ImSessionContext();
		channelContext.setAttribute(imSessionContext);

		ImUtils.setClient(channelContext);

		ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;

		if (jFrameMain.getListModel().contains(clientChannelContext)) {
			WriteLock writeLock = JFrameMain.updatingListLock.writeLock();
			writeLock.lock();

			try {
				jFrameMain.getClients().repaint();
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				writeLock.unlock();
			}
		}

		JFrameMain.isNeedUpdateConnectionCount = true;
		if (isReconnect && isConnected) {
			JFrameMain.isNeedUpdateList = true;
		}

		if (!isConnected) {
			//没连上
			return;
		}

		Tio.send(channelContext, handshakeReqPacket);

		return;
	}



	/**
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 *
	 * @param channelContext
	 * @param packet
	 * @author tanyaowu
	 * 2016年12月20日 上午11:41:27
	 *
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
		ImPacket imPacket = (ImPacket) packet;
		if (isSentSuccess) {
			CommandStat.getCount(imPacket.getCommand()).sent.incrementAndGet();
			JFrameMain.sentPackets.incrementAndGet();

			JFrameMain.isNeedUpdateSentCount = true;

		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {

		ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute();
		imSessionContext.setHandshaked(false);

		JFrameMain jFrameMain = JFrameMain.getInstance();

		WriteLock updatingListWriteLock = JFrameMain.updatingListLock.writeLock();
		DefaultListModel<ClientChannelContext> listModel = jFrameMain.getListModel();

		if (listModel.contains(channelContext)) {
			updatingListWriteLock.lock();
			JList<ClientChannelContext> clients = jFrameMain.getClients();

			try {
				if (isRemove) {
					listModel.removeElement(channelContext);
				}

				ClientGroupContext clientGroupContext = (ClientGroupContext) channelContext.groupContext;
				SetWithLock<ChannelContext> setWithLock = clientGroupContext.connections;
				Set<ChannelContext> set = setWithLock.getObj();
				ReadLock readLock = setWithLock.getLock().readLock();

				try {
					readLock.lock();
					for (ChannelContext channelContext1 : set) {
						if (!listModel.contains(channelContext1)) {
							if (listModel.size() < JFrameMain.MAX_LIST_COUNT) {
								listModel.addElement((ClientChannelContext) channelContext1);
							} else {
								break;
							}
						}
					}
				} catch (Exception e) {
					log.error(e.toString(), e);
				} finally {
					readLock.unlock();
				}
				jFrameMain.getClients().repaint();

			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				updatingListWriteLock.unlock();
			}
		} else {
			//updatingListWriteLock.unlock();
		}

		JFrameMain.isNeedUpdateConnectionCount = true;
	
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {

		ImPacket imPacket = (ImPacket) packet;
		CommandStat.getCount(imPacket.getCommand()).received.incrementAndGet();
		//		org.tio.examples.im.client.ui.JFrameMain.updateReceivedLabel();

		JFrameMain.isNeedUpdateReceivedCount = true;

		long receivedPacket = JFrameMain.receivedPackets.incrementAndGet();
		long sentPacket = JFrameMain.sentPackets.get();
		if (receivedPacket <= 10 || sentPacket <= 10) {
			return;
		}

		long time = SystemTimer.currTime;

		JFrameMain frameMain = JFrameMain.getInstance();
		if (receivedPacket % 100000 == 0) {
			long sendStartTime = frameMain.getSendStartTime();
			long in = time - sendStartTime;
			if (in <= 0) {
				in = 1;
			}

			long initReceivedBytes = frameMain.getStartRecievedBytes();
			long initSentBytes = frameMain.getStartSentBytes();

			long nowReceivedBytes = channelContext.groupContext.groupStat.receivedBytes.get();
			long nowSentBytes = channelContext.groupContext.groupStat.sentBytes.get();

			long receivedBytes = nowReceivedBytes - initReceivedBytes;
			long sentBytes = nowSentBytes - initSentBytes;

			double perReceivedPacket = Math.ceil((double) receivedPacket / (double) in * 1000);
			double perReceivedBytes = Math.ceil((double) receivedBytes / (double) in * 1000);

			double perSentPacket = Math.ceil((double) sentPacket / (double) in * 1000);
			double perSentBytes = Math.ceil((double) sentBytes / (double) in * 1000);

			NumberFormat numberFormat = NumberFormat.getInstance();

			//汇总：耗时31毫秒、接收消息100条共10KB，发送消息100条共30KB
			//每秒：接收消息100条共10KB，发送消息100条共30KB
			log.error("\r\n汇总：耗时{}毫秒，接收消息{}条共{}B，发送消息{}条共{}B \r\n" + "每秒：接收消息{}条共{}B，发送消息{}条共{}B\r\n" + "接收消息每条平均{}B，发送消息每条平均{}B\r\n", numberFormat.format(in),
					numberFormat.format(receivedPacket), numberFormat.format(receivedBytes), numberFormat.format(sentPacket), numberFormat.format(sentBytes),
					numberFormat.format(perReceivedPacket), numberFormat.format(perReceivedBytes), numberFormat.format(perSentPacket), numberFormat.format(perSentBytes),
					numberFormat.format(Math.ceil(receivedBytes / receivedPacket)), numberFormat.format(Math.ceil(sentBytes / sentPacket)));
		}

	
		
	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
