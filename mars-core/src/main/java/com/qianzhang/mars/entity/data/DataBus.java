/**
 *
 *
 * @author qianzhang
 *
 * @Date 2020/4/1
 */
package com.qianzhang.mars.entity.data;

import cn.hutool.core.util.ObjectUtil;
import com.qianzhang.mars.property.LiteflowConfig;
import com.qianzhang.mars.property.LiteflowConfigGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 数据BUS，主要用来管理Slot，用以分配和回收
 * @author qianzhang
 */
public class DataBus {

	private static final Logger LOG = LoggerFactory.getLogger(DataBus.class);

	public static AtomicInteger OCCUPY_COUNT = new AtomicInteger(0);

	private static final AtomicReferenceArray<Slot> SLOTS;

	private static final ConcurrentLinkedQueue<Integer> QUEUE;

	static {
		LiteflowConfig liteflowConfig = LiteflowConfigGetter.get();
		int slotSize = liteflowConfig.getSlotSize();
		SLOTS = new AtomicReferenceArray<>(slotSize);
		QUEUE = IntStream.range(0, slotSize - 1).boxed().collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
	}

	public static int offerSlot(Class<? extends Slot> slotClazz) {
		try {
			Slot slot = slotClazz.newInstance();
			Integer slotIndex = QUEUE.poll();
			if (ObjectUtil.isNotNull(slotIndex) && SLOTS.compareAndSet(slotIndex, null, slot)) {
				OCCUPY_COUNT.incrementAndGet();
				return slotIndex;
			}
		} catch (Exception e) {
			LOG.error("offer slot error", e);
			return -1;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Slot> T getSlot(int slotIndex){
		return (T)SLOTS.get(slotIndex);
	}

	public static void releaseSlot(int slotIndex){
		if(ObjectUtil.isNotNull(SLOTS.get(slotIndex))){
			LOG.info("[{}]:slot[{}] released",SLOTS.get(slotIndex).getRequestId(),slotIndex);
			SLOTS.set(slotIndex, null);
			QUEUE.add(slotIndex);
			OCCUPY_COUNT.decrementAndGet();
		}else{
			LOG.warn("slot[{}] already has been released",slotIndex);
		}
	}
}
