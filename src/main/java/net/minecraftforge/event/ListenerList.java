package net.minecraftforge.event;

import java.util.ArrayList;
import java.util.Collection;

public class ListenerList {
    private static ArrayList<ListenerList> allLists = new ArrayList();
    private static int maxSize = 0;
    private ListenerList parent;
    private ListenerList.ListenerListInst[] lists = new ListenerList.ListenerListInst[0];

    public ListenerList() {
        allLists.add(this);
        this.resizeLists(maxSize);
    }

    public ListenerList(ListenerList parent) {
        allLists.add(this);
        this.parent = parent;
        this.resizeLists(maxSize);
    }

    public static void resize(int max) {
        if (max > maxSize) {
            for(ListenerList list : allLists) {
                list.resizeLists(max);
            }

            maxSize = max;
        }
    }

    public void resizeLists(int max) {
        if (this.parent != null) {
            this.parent.resizeLists(max);
        }

        if (this.lists.length < max) {
            ListenerList.ListenerListInst[] newList = new ListenerList.ListenerListInst[max];

            int x;
            for(x = 0; x < this.lists.length; ++x) {
                newList[x] = this.lists[x];
            }

            for(; x < max; ++x) {
                if (this.parent != null) {
                    newList[x] = new ListenerList.ListenerListInst(this.parent.getInstance(x));
                } else {
                    newList[x] = new ListenerList.ListenerListInst();
                }
            }

            this.lists = newList;
        }
    }

    public static void clearBusID(int id) {
        for(ListenerList list : allLists) {
            list.lists[id].dispose();
        }

    }

    protected ListenerList.ListenerListInst getInstance(int id) {
        return this.lists[id];
    }

    public IEventListener[] getListeners(int id) {
        return this.lists[id].getListeners();
    }

    public void register(int id, EventPriority priority, IEventListener listener) {
        this.lists[id].register(priority, listener);
    }

    public void unregister(int id, IEventListener listener) {
        this.lists[id].unregister(listener);
    }

    public static void unregiterAll(int id, IEventListener listener) {
        for(ListenerList list : allLists) {
            list.unregister(id, listener);
        }

    }

    private class ListenerListInst {
        private boolean rebuild = true;
        private IEventListener[] listeners;
        private ArrayList<ArrayList<IEventListener>> priorities;
        private ListenerList.ListenerListInst parent;

        private ListenerListInst() {
            int count = EventPriority.values().length;
            this.priorities = new ArrayList(count);

            for(int x = 0; x < count; ++x) {
                this.priorities.add(new ArrayList());
            }

        }

        public void dispose() {
            for(ArrayList<IEventListener> listeners : this.priorities) {
                listeners.clear();
            }

            this.priorities.clear();
            this.parent = null;
            this.listeners = null;
        }

        private ListenerListInst(ListenerList.ListenerListInst parent) {
            this();
            this.parent = parent;
        }

        public ArrayList<IEventListener> getListeners(EventPriority priority) {
            ArrayList<IEventListener> ret = new ArrayList((Collection)this.priorities.get(priority.ordinal()));
            if (this.parent != null) {
                ret.addAll(this.parent.getListeners(priority));
            }

            return ret;
        }

        public IEventListener[] getListeners() {
            if (this.shouldRebuild()) {
                this.buildCache();
            }

            return this.listeners;
        }

        protected boolean shouldRebuild() {
            return this.rebuild || this.parent != null && this.parent.shouldRebuild();
        }

        private void buildCache() {
            ArrayList<IEventListener> ret = new ArrayList();

            for(EventPriority value : EventPriority.values()) {
                ret.addAll(this.getListeners(value));
            }

            this.listeners = (IEventListener[])ret.toArray(new IEventListener[0]);
            this.rebuild = false;
        }

        public void register(EventPriority priority, IEventListener listener) {
            ((ArrayList)this.priorities.get(priority.ordinal())).add(listener);
            this.rebuild = true;
        }

        public void unregister(IEventListener listener) {
            for(ArrayList<IEventListener> list : this.priorities) {
                list.remove(listener);
            }

        }
    }
}
