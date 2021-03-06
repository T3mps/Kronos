package net.acidfrog.kronos.physics.collision.broadphase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.Graphics2D;
import net.acidfrog.kronos.core.lang.annotations.Debug;
import net.acidfrog.kronos.math.Mathk;
import net.acidfrog.kronos.physics.collision.CollisionPair;
import net.acidfrog.kronos.physics.geometry.AABB;
import net.acidfrog.kronos.physics.geometry.Ray;

public final class DynamicAABBTree<T extends BroadphaseMember> extends BroadphaseDetector<T> {

    private final Comparator<DynamicAABBTreeLeaf<T>> leafComparator = new Comparator<DynamicAABBTreeLeaf<T>>() {

        @Override
        public int compare(DynamicAABBTreeLeaf<T> leaf1, DynamicAABBTreeLeaf<T> leaf2) {
            return Mathk.sign(leaf2.aabb.getPerimeter() - leaf1.aabb.getPerimeter());
        }
    };

    private DynamicAABBTreeNode root;
    private boolean autoRebalance;

    private final Map<T, DynamicAABBTreeLeaf<T>> leaves;

    public DynamicAABBTree() {
        this(new DynamicAABBPolicy(), BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
    }

    public DynamicAABBTree(boolean autoRebalance) {
        this(new DynamicAABBPolicy(), BroadphaseDetector.DEFAULT_INITIAL_CAPACITY, autoRebalance);
    }

    public DynamicAABBTree(int initialCapacity) {
        this(new DynamicAABBPolicy(), initialCapacity);
    }

    public DynamicAABBTree(AABBPolicy policy) {
        this(policy, BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
    }

    public DynamicAABBTree(int initialCapacity, boolean autoRebalance) {
        this(new DynamicAABBPolicy(), initialCapacity, autoRebalance);
    }

    public DynamicAABBTree(AABBPolicy policy, boolean autoRebalance) {
        this(policy, BroadphaseDetector.DEFAULT_INITIAL_CAPACITY, autoRebalance);
    }

    public DynamicAABBTree(AABBPolicy policy, int initialCapacity) {
        this(policy, initialCapacity, false);
    }

    public DynamicAABBTree(AABBPolicy policy, int initialCapacity, boolean autoRebalance) {
        super(policy);
        // 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75f according to the javadocs, but lets assign it to be sure
        this.leaves  = new LinkedHashMap<T, DynamicAABBTreeLeaf<T>>(initialCapacity * 4 / 3 + 1, 0.75f);
        this.autoRebalance = autoRebalance;
    }

    @Debug
    public void render(Graphics2D g2d) {
        if (root != null) root.render(g2d);
    }

    @Override
    public void add(T t) {
        DynamicAABBTreeLeaf<T> leaf = leaves.get(t);

        if (leaf != null) updateNode(t, leaf);
        else addNode(t);
    }

    /**
     * Prepares a member for insertion into the tree.
     * 
     * @param t The member to add.
     */
    private void addNode(T t) {
        // get the aabb and expand it by the policy
        AABB aabb = t.getBounds();
        policy.enforce(aabb);

        // create the leaf & add it to the tree
        DynamicAABBTreeLeaf<T> leaf = new DynamicAABBTreeLeaf<T>(t);
        leaf.aabb.set(aabb);
        leaves.put(t, leaf);
        
        // insert the node into the tree
        insert(leaf);
    }

    /**
     * Inserts a node into the tree.
     * 
     * @param member The node to insert.
     */
    private void insert(DynamicAABBTreeNode node) {
        // check for root
        if (root == null) {
            root = node;
            return;
        }

        AABB t = new AABB();
        AABB memberAABB = node.aabb;

        // start with the root
        DynamicAABBTreeNode current = root;
        
        // look for the best place to insert the node
        while (!current.isLeaf()) {
            // get the current aabb
            AABB aabb = current.aabb;

            // get the compairison heuristic
            float perimeter = aabb.getPerimeter();
            float unionPerimeter = t.set(aabb).union(memberAABB).getPerimeter();
            float cost = 2 * unionPerimeter;

            // get the cost of descending farther down
            float descendCost = 2 * (unionPerimeter - perimeter);
            
            DynamicAABBTreeNode left = current.left;
            DynamicAABBTreeNode right = current.right;

            // get left cost
            float costl = 0.0f;
            if (left.isLeaf()) {
                costl = t.union(left.aabb, memberAABB).getPerimeter() + descendCost;
            } else {
                float oldPerimeter = left.aabb.getPerimeter();
                float newPerimeter = t.union(left.aabb, memberAABB).getPerimeter();
                costl = (newPerimeter - oldPerimeter) + descendCost;
            }

            // get right cost
            float costr = 0.0f;
            if (right.isLeaf()) {
                costr = t.union(right.aabb, memberAABB).getPerimeter() + descendCost;
            } else {
                float oldPerimeter = right.aabb.getPerimeter();
                float newPerimeter = t.union(right.aabb, memberAABB).getPerimeter();
                costr = (newPerimeter - oldPerimeter) + descendCost;
            }

            // if the cost to create a new parent node is less than the cost to
            // descend
            if (cost < costl && cost < costr) break;

            // otherwise, continue down the tree
            if (costl < costr) current = left;
            else current = right;
        }

        // insert new parent node for the member
        DynamicAABBTreeNode newParent = new DynamicAABBTreeNode();
        DynamicAABBTreeNode parent = newParent.parent = current.parent;

        // set the new parent's attributes
        newParent.aabb.union(current.aabb, memberAABB);
        newParent.height = current.height + 1;

        if (parent != null) {
            //  node isn't the root
            if (parent.left == current) parent.left = newParent;
            else parent.right = newParent;

            newParent.left = current;
            newParent.right = node;
            current.parent = newParent;
            node.parent = newParent;
        } else {
            // node is the root
            newParent.left = current;
            newParent.right = node;
            current.parent = newParent;
            node.parent = newParent;
            root = newParent;
        }

        // fix the heights
        current = node.parent;
        while (current != null) {
            current = balance(current);

            DynamicAABBTreeNode left = current.left;
            DynamicAABBTreeNode right = current.right;
            
            current.height = 1 + Mathk.max(left.height, right.height);
            current.aabb.union(left.aabb, right.aabb);

            current = current.parent;
        }
    }

    /**
     * Balances the subtree using the specified node as the root.
     * 
     * @param node the root of the subtree to balance
     * @return the new root of the subtree
     */
    private DynamicAABBTreeNode balance(DynamicAABBTreeNode node) {
        // check for leaf node or height difference of 2
        // if the height difference is 2, there are not
        // enough children to be unbalanced
        if (node.isLeaf() || node.height < 2) return node;

        DynamicAABBTreeNode a = node;
        DynamicAABBTreeNode b = a.left;
        DynamicAABBTreeNode c = a.right;

        // balance factor, if positive, balance is off
        // on the right side; if negative, the left.
        int factor = c.height - b.height;

        // right side
        if (factor > 1) {
            DynamicAABBTreeNode d = c.left;
            DynamicAABBTreeNode e = c.right;

            // switch a and c
            c.left = a;
            c.parent = a.parent;
            a.parent = c;

            // change c's parent to point to c
            if (c.parent != null) {
                if (c.parent.left == a) c.parent.left = c;
                else c.parent.right = c;
            } else {
                // if parent is null, c is the new root
                root = c;
            }

            if (d.height > e.height) {
                // rotate left
                c.right = d;
                a.right = e;
                e.parent = a;

                // update the aabbs
                a.aabb.union(b.aabb, e.aabb);
                c.aabb.union(a.aabb, d.aabb);

                // update the heights
                a.height = 1 + Mathk.max(b.height, e.height);
                c.height = 1 + Mathk.max(a.height, d.height);
            } else {
                // rotate right
                c.right = e;
                a.right = d;
                d.parent = a;

                // update the aabbs
                a.aabb.union(b.aabb, d.aabb);
                c.aabb.union(a.aabb, e.aabb);

                // update the heights
                a.height = 1 + Mathk.max(b.height, d.height);
                c.height = 1 + Mathk.max(a.height, e.height);
            }

            return c;
        }

        // left side
        if (factor < -1) {
            DynamicAABBTreeNode f = b.left;
            DynamicAABBTreeNode g = b.right;

            // switch a and b
            b.left = a;
            b.parent = a.parent;
            a.parent = b;

            // change b's parent to point to b
            if (b.parent != null) {
                if (b.parent.left == a) b.parent.left = b;
                else b.parent.right = b;
            } else {
                // if parent is null, b is the new root
                root = b;
            }

            if (f.height > g.height) {
                // rotate left
                b.right = f;
                a.left = g;
                g.parent = a;

                // update the aabbs
                a.aabb.union(c.aabb, g.aabb);
                b.aabb.union(a.aabb, f.aabb);

                // update the heights
                a.height = 1 + Mathk.max(c.height, g.height);
                b.height = 1 + Mathk.max(a.height, f.height);
            } else {
                // rotate right
                b.right = g;
                a.left = f;
                f.parent = a;

                // update the aabbs
                a.aabb.union(c.aabb, f.aabb);
                b.aabb.union(a.aabb, g.aabb);

                // update the heights
                a.height = 1 + Mathk.max(c.height, f.height);
                b.height = 1 + Mathk.max(a.height, g.height);
            }

            return b;
        }

        // no balancing needed
        return a;
    }

    @Override
    public boolean remove(T t) {
        DynamicAABBTreeLeaf<T> leaf = leaves.remove(t);

        if (leaf != null) {
            remove(leaf);
            return true;
        }

        return false;
    }

    private void remove(DynamicAABBTreeNode node) {
        if (root == null) return;

        if (node == root) {
            root = null;
            return;
        }

        DynamicAABBTreeNode parent = node.parent;
        DynamicAABBTreeNode grandparent = parent.parent;
        DynamicAABBTreeNode sibling = (node == parent.left) ? parent.right : parent.left;

        // check if parent is root
        if (grandparent != null) {
            if (grandparent.left == parent) grandparent.left = sibling;
            else grandparent.right = sibling;

            sibling.parent = grandparent;

            // rebalance the tree
            DynamicAABBTreeNode current = grandparent;
            while (current != null) {
                current = balance(current);

                DynamicAABBTreeNode left = current.left;
                DynamicAABBTreeNode right = current.right;

                current.height = 1 + Mathk.max(left.height, right.height);
                current.aabb.union(left.aabb, right.aabb);

                current = current.parent;
            }
        } else {
            // if parent is root, sibling becomes new root
            root = sibling;
            sibling.parent = null;
        }
    }

    @Override
    public void update() {
        for (DynamicAABBTreeLeaf<T> leaf : leaves.values()) update(leaf.member);

        if (autoRebalance) { recompute(); }
    }

    @Override
    public void update(T t) {
        DynamicAABBTreeLeaf<T> leaf = leaves.get(t);

        if (leaf != null) updateNode(t, leaf);
        // if it doesn't exist, add it
        else addNode(t);
    }

    private void updateNode(T t, DynamicAABBTreeLeaf<T> node) {
        // get and update based on policy
        AABB aabb = t.getBounds();
        
        boolean containsUnexpanded = node.aabb.contains(aabb);
        
        policy.enforce(aabb);

        if (containsUnexpanded) {
            // we could stop here and conclude that there's nothing to do, but
			// there's an edge case where the current AABB is MUCH larger than
			// the new AABB and it never gets sized down.  This has the effect
			// of sending a lot more pairs to the narrow phase until the object
			// moves out of the current AABB.  If this doesn't happen, for example,
			// object stops, then the broadphase retains the large AABB forever.
			
			// so the goal here is to understand and adapt the larger AABBs to 
			// smaller ones based on their perimeter ratio
			float p0 = node.aabb.getPerimeter();
			float p1 = aabb.getPerimeter();
			float ratio = p0 / p1;
			if (ratio <= AABB_REDUCTION_RATIO) {
				// if the old AABB is 2x (or less) the size (in perimeter) to the new
				// then we'll accept it and not update
				return;
			}
        }

        // re-insert the node
        remove(node);
        node.aabb.set(aabb);
        insert(node);
    }

    @Override
    public boolean contains(T t) {
        return leaves.containsKey(t);
    }

    @Override
    public void clear() {
        leaves.clear();
        root = null;
    }

    @Override
    public int size() {
        return leaves.size();
    }

    @Override
    public AABB get(T t) {
        DynamicAABBTreeLeaf<T> leaf = leaves.get(t);

        if (leaf != null) {
            return leaf.aabb;
        } else {
            AABB aabb = t.getBounds();

            if (aabb.isDegenerate()) return aabb;

            policy.enforce(aabb);
            return aabb;
        }
    }

    @Override
    public void recompute() {
        if (root == null) return;

        root = null;

        List<DynamicAABBTreeLeaf<T>> leaves = new ArrayList<DynamicAABBTreeLeaf<T>>(this.leaves.values());

        // sort the leaves by perimeter
        Collections.sort(leaves, leafComparator);

        // add the leaves back in
        for (DynamicAABBTreeLeaf<T> leaf : leaves) {
            leaf.height = 0;
            leaf.left = null;
            leaf.right = null;
            leaf.parent = null;
            insert(leaf);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollisionPair<T>> detect() {
        if (root == null) return new ArrayList<CollisionPair<T>>();
        
        List<CollisionPair<T>> pairs = new ArrayList<CollisionPair<T>>(BroadphaseDetector.getEstimatedCollisionPairs(leaves.size()));

        // detect all pairs

        DynamicAABBTreeLeaf<T>[] leaves = this.leaves.values().toArray(new DynamicAABBTreeLeaf[0]);

        for (int i = 0; i < leaves.length; i++) {
            DynamicAABBTreeLeaf<T> a = leaves[i];

            for (int j = i + 1; j < leaves.length; j++) {
                DynamicAABBTreeLeaf<T> b = leaves[j];

                if (a.aabb.intersects(b.aabb)) {
                    pairs.add(new BroadphasePair<T>(a.member, b.member));
                }
            }
        }

        return pairs;
    }

    @Override
    public List<T> raycast(Ray ray, float maxDistance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int height() {
        if (root == null) return 0;
        return root.height;
    }

    /**
	 * Returns a quality metric for the tree.
     * 
     * @return float
	 */
    public float getPerimeterRatio() {
        if (root == null) return 0;
        return perimeterRatio(root) / root.aabb.getPerimeter();
    }

    /**
     * Returns a quality metric for the subtree, rooted at the given node.
     * 
     * @param node {@link DynamicAABBTreeNode}
     * @return float
     */
    private float perimeterRatio(DynamicAABBTreeNode node) {
        if (node.isLeaf()) return 0;

        float ratio = node.aabb.getPerimeter();

        ratio += perimeterRatio(node.left);
        ratio += perimeterRatio(node.right);

        return ratio;
    }

}
