/**
 * 查询 tree node
 */
export function findNode(nodes, findKey) {
  // 查询当前层
  for (const node of nodes) {
    if (node.key === findKey) {
      return node
    }
  }
  // 查询子节点
  for (const node of nodes) {
    if (node.children && node.children.length) {
      const findValue = findNode(node.children, findKey)
      if (findValue) {
        return findValue
      }
    }
  }
}

/**
 * 查询 tree parent node
 */
export function findParentNode(nodes, findKey) {
  // 查询当前层
  for (const node of nodes) {
    if (node.children && node.children.length) {
      const filterNodes = node.children.filter(childrenNode => childrenNode.key === findKey)
      if (filterNodes.length) {
        return node
      }
    }
  }
  // 查询子节点
  for (const node of nodes) {
    if (node.children && node.children.length) {
      const findValue = findParentNode(node.children, findKey)
      if (findValue) {
        return findValue
      }
    }
  }
}

/**
 * 获取子节点的 key
 */
export function getChildNodeKeys(node, keys = []) {
  if (node.children && node.children.length) {
    // 查询当前层
    for (const child of node.children) {
      keys.push(child.key)
    }
    // 查询子节点
    for (const child of node.children) {
      getChildNodeKeys(child, keys)
    }
  }
  return keys
}

/**
 * 填充 tree属性
 */
export function setTreeDataProps(tree, filler) {
  if (tree != null) {
    tree.forEach(node => {
      filler(node)
      setTreeDataProps(node.children, filler)
    })
  }
  return tree || []
}

/**
 * 获取深度 key
 */
export function getDepthKeys(tree, depth, keys = [], curr = 1) {
  if (curr > depth) {
    return keys
  }
  for (const node of tree) {
    keys.push(node.key)
    if (node.children && node.children.length) {
      getDepthKeys(node.children, depth, keys, curr + 1)
    }
  }
  return keys
}

/**
 * 设置 tree 全路径
 */
export function setTreePath(tree) {
  if (!tree || !tree.length) {
    return []
  }
  const loop = (node, parents = []) => {
    const current = [...parents, node.title]
    if (node.children && node.children.length) {
      for (const child of node.children) {
        loop(child, current)
      }
    }
    node.path = current.join('/')
    return current
  }
  tree.forEach(s => loop(s))
  return tree || []
}
