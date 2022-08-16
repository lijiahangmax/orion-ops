import { Chart } from '@antv/g2'
import { formatSecond } from '@/lib/utils'

/**
 * 渲染时间戳对象折现图
 */
export function timestampRender(id, chatObject, chartField, timeFormatter, valueFormatter, tooltipFormatter, renderData) {
  let chart = chatObject[chartField]
  const needInit = !chart
  if (needInit) {
    chart = chatObject[chartField] = new Chart({
      container: id,
      autoFit: true,
      padding: [4, 8, 4, 8]
    })
    chart.animate(false)
    chart.scale({
      time: {
        tickCount: 6
      },
      value: {
        nice: true
      }
    })
    if (timeFormatter) {
      chart.axis('time', {
        label: {
          formatter: timeFormatter
        }
      })
    } else {
      chart.axis('time', false)
    }
    if (valueFormatter) {
      chart.axis('value', {
        label: {
          formatter: valueFormatter
        }
      })
    } else {
      chart.axis('value', false)
    }
    chart.tooltip({
      title: (title, datum) => formatSecond(datum.time, 'yyyy-MM-dd HH:mm:ss'),
      customItems: (items) => {
        tooltipFormatter(items[0])
        return items
      }
    })
    chart.line().position('time*value')
    chart.data(renderData)
    chart.render()
  } else {
    chart.changeData(renderData)
    chart.render()
  }
}
