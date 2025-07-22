import type {GroupComplete} from "@/dto/Group";

export function getBoolStr(state: boolean | undefined): string {
    return state ? 'oui' : 'non'
}

export function getSortedGroups(groups: GroupComplete[]): GroupComplete[] {
    /**
     * Sort groups to have pass three days in first, and sorted by draw order
     */
    const grouped: { [p: string]: GroupComplete[] } = groups.reduce((acc : { [p: string]: GroupComplete[] }, g) => {
        const passType = g.members[0].pass
        acc[passType] = (acc[passType] || []).concat(g);
        return acc;
    }, {})

    for (const pass in grouped) {
        grouped[pass].sort((a, b) => (a.group.drawOrder ?? Infinity) - (b.group.drawOrder ?? Infinity));
    }

    return Object.values(grouped).flat()
}

export function animateChevron(id: string) {
    const t = document.getElementById(id)
    if (t == null) return
    t.classList.contains("rotate-180") ? t.classList.remove("rotate-180") : t.classList.add("rotate-180")
}