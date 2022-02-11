package com.orion.ops.handler.app.release;

import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.app.release.machine.IMachineProcessor;
import com.orion.utils.io.Streams;

/**
 * 串行处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 15:33
 */
public class SerialReleaseProcessor extends AbstractReleaseProcessor {

    public SerialReleaseProcessor(Long releaseId) {
        super(releaseId);
    }

    @Override
    protected void handler() throws Exception {
        Exception ex = null;
        for (IMachineProcessor machineProcessor : store.getMachineProcessors().values()) {
            if (ex != null || terminated) {
                machineProcessor.skipped();
                continue;
            }
            try {
                machineProcessor.run();
            } catch (Exception e) {
                ex = e;
            } finally {
                Streams.close(machineProcessor);
            }
        }
        if (ex != null && !terminated) {
            throw ex;
        }
    }

    @Override
    public void terminated() {
        super.terminated();
        // 获取当前执行中的机器执行器
        store.getMachineProcessors().values().stream()
                .filter(s -> s.getStatus().equals(ActionStatus.RUNNABLE))
                .forEach(IMachineProcessor::terminated);
    }

}
